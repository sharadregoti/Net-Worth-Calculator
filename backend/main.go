package main

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"regexp"
	"strconv"
	"strings"

	"github.com/urfave/cli"
	"golang.org/x/oauth2"
	"golang.org/x/oauth2/google"
	"google.golang.org/api/gmail/v1"
	"google.golang.org/api/option"
)

var essentialFlags = []cli.Flag{
	cli.StringFlag{
		Name:   "log-level",
		EnvVar: "LOG_LEVEL",
		Usage:  "Set the log level [debug | info | error]",
	},
}

func main() {
	app := cli.NewApp()
	app.Version = "0.0.1"
	app.Name = "Net worth calculator"
	app.Usage = "core binary to run net worth calculator"

	app.Commands = []cli.Command{
		{
			Name:   "run",
			Usage:  "runs the space cloud instance",
			Action: actionRun,
			Flags:  essentialFlags,
		},
	}

	err := app.Run(os.Args)
	if err != nil {
		log.Fatal(err)
	}
}

// Retrieve a token, saves the token, then returns the generated client.
func getClient(config *oauth2.Config) *http.Client {
	// The file token.json stores the user's access and refresh tokens, and is
	// created automatically when the authorization flow completes for the first
	// time.
	tokFile := "token.json"
	tok, err := tokenFromFile(tokFile)
	if err != nil {
		tok = getTokenFromWeb(config)
		saveToken(tokFile, tok)
	}
	return config.Client(context.Background(), tok)
}

// Request a token from the web, then returns the retrieved token.
func getTokenFromWeb(config *oauth2.Config) *oauth2.Token {
	authURL := config.AuthCodeURL("state-token", oauth2.AccessTypeOffline)
	fmt.Printf("Go to the following link in your browser then type the "+
		"authorization code: \n%v\n", authURL)

	var authCode string
	if _, err := fmt.Scan(&authCode); err != nil {
		log.Fatalf("Unable to read authorization code: %v", err)
	}

	tok, err := config.Exchange(context.TODO(), authCode)
	if err != nil {
		log.Fatalf("Unable to retrieve token from web: %v", err)
	}
	return tok
}

// Retrieves a token from a local file.
func tokenFromFile(file string) (*oauth2.Token, error) {
	f, err := os.Open(file)
	if err != nil {
		return nil, err
	}
	defer f.Close()
	tok := &oauth2.Token{}
	err = json.NewDecoder(f).Decode(tok)
	return tok, err
}

// Saves a token to a file path.
func saveToken(path string, token *oauth2.Token) {
	fmt.Printf("Saving credential file to: %s\n", path)
	f, err := os.OpenFile(path, os.O_RDWR|os.O_CREATE|os.O_TRUNC, 0600)
	if err != nil {
		log.Fatalf("Unable to cache oauth token: %v", err)
	}
	defer f.Close()
	json.NewEncoder(f).Encode(token)
}

func actionRun(c *cli.Context) error {

	ctx := context.Background()
	b, err := ioutil.ReadFile("/home/sharad/open-source/Net-worth-calculator/backend/assets/gcp-net-worth-calculator-desktop-app-oauth-credentials.json")
	if err != nil {
		log.Fatalf("Unable to read client secret file: %v", err)
	}

	// If modifying these scopes, delete your previously saved token.json.
	config, err := google.ConfigFromJSON(b, gmail.GmailReadonlyScope)
	if err != nil {
		log.Fatalf("Unable to parse client secret file to config: %v", err)
	}
	client := getClient(config)

	srv, err := gmail.NewService(ctx, option.WithHTTPClient(client))
	if err != nil {
		log.Fatalf("Unable to retrieve Gmail client: %v", err)
	}
	// {subject: (Transaction alert for your ICICI Bank debit card) subject: (Transaction alert for your ICICI Bank credit card)}
	// {subject: (Transaction alert for your State Bank of India Debit Card)}
	user := "me"
	res, err := srv.Users.Messages.List(user).Q("{subject: (Transaction alert for your State Bank of India Debit Card)}").MaxResults(1).Do()
	if err != nil {
		log.Fatalf("Unable to retrieve Gmail messages: %v", err)
	}

	log.Printf("Processing %v messages...\n", len(res.Messages))
	for _, m := range res.Messages {
		msg, err := srv.Users.Messages.Get("me", m.Id).Do()
		if err != nil {
			log.Fatalf("Unable to retrieve message %v: %v", m.Id, err)
		}
		println("Subject :", getMessageSubject(msg.Payload.Headers))
		println("Payload Type :", msg.Payload.MimeType)
		msgBody, _ := base64.RawURLEncoding.DecodeString(getMessageBody(msg.Payload.Parts))
		print(string(msgBody))
		switch msg.Payload.MimeType {
		case "text/plain":
			str := string(msgBody)
			re := regexp.MustCompile(`[-]?\d[\d,]*[\.]?[\d{2}]*`)

			fmt.Printf("Pattern: %v\n", re.String())                           // Print Pattern
			fmt.Printf("String contains any match: %v\n", re.MatchString(str)) // True

			res := ""
			submatchall := re.FindAllString(str, -1)
			println("String Number :", submatchall)
			res = strings.Replace(submatchall[0], ",", "", -1)
			res = strings.Split(res, ".")[0]
			number, err := strconv.Atoi(res)
			if err != nil {
				return err
			}
			println("Number :", number)

		}
	}
	return nil
}

// getMessageBody finds the HTML body of an email.
func getMessageBody(parts []*gmail.MessagePart) string {
	for _, part := range parts {
		if len(part.Parts) > 0 {
			return getMessageBody(part.Parts)
		} else {
			if part.MimeType == "text/html" {
				return part.Body.Data
			}
			println(part.MimeType)
		}
	}

	return ""
}

// getMessageSubject goes through the headers to find the Subject header.
func getMessageSubject(headers []*gmail.MessagePartHeader) string {
	return getMessageHeader(headers, "Subject")
}

// getMessageHeader goes through a list of headers and returns the header where
// the name matches the one we want.
func getMessageHeader(headers []*gmail.MessagePartHeader, wanted string) string {
	for _, header := range headers {
		if header.Name == wanted {
			return header.Value
		}

	}

	return ""
}
